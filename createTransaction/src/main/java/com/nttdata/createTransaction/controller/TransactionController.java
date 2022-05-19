package com.nttdata.createTransaction.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.nttdata.createTransaction.entity.Product;
import com.nttdata.createTransaction.entity.Transaction;
import com.nttdata.createTransaction.repository.ProductRepository;
import com.nttdata.createTransaction.repository.TransactionRepository;
import com.nttdata.createTransaction.service.ProductService;
import com.nttdata.createTransaction.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProductService  productService;

    @Autowired
    private TransactionRepository transRepo;

    @Autowired
    private ProductRepository productRepo;
    
    //CRUD
    @GetMapping(value = "/all")
    public List<Transaction> getAll() {
        log.info("lista todos");
        return transactionService.getAll();
    }  

    @GetMapping("getTransaction/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getData(@PathVariable("id") String id){
      Map<String, Object> salida = new HashMap<>();
      Optional<Transaction> trans_doc = transRepo.findById(id);
      if (trans_doc.isPresent()) {
        salida.put("transaction", trans_doc);
      }else{
        salida.put("status", "Id de transacción no encontrado");
      }
      return ResponseEntity.ok(salida);
    }



    // @PostMapping(value = "/create")
    // public Transaction createTransaction(@RequestBody Transaction new_trans){
    //     new_trans.setStatus("ACTIVE");
    //     return transactionService.createTransaction(new_trans);
    // }


    @PutMapping("/update/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable("id") String id, @RequestBody Transaction temp) {
      Optional<Transaction> transaction = transRepo.findById(id);
      if (transaction.isPresent()) {
        temp.setId(id);
        return new ResponseEntity<>(transactionService.createTransaction(temp), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    }

    @PutMapping("setInactive/{id}")
    public ResponseEntity<Transaction> setInactive(@PathVariable("id") String id, @RequestBody Transaction temp_trans) {
      Optional<Transaction> trans_doc = transRepo.findById(id);
      if (trans_doc.isPresent()) {
        Transaction _trans = trans_doc.get();
        _trans.setStatus("INACTIVE");
        return new ResponseEntity<>(transRepo.save(_trans), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } 


    // Crear Servicio para Registrar transacciones: depósito, retiro, pago , consumo
    // Campos Obligatorios: Id cliente (valido) , tipo de cuenta, monto

    //Clase interna para validar producto bancario
    public HashMap<String, Object> validateProduct(String id) {        
      HashMap<String, Object> map = new HashMap<>();
      Optional<Product> doc = productRepo.findById(id);
      if (doc.isPresent()) {
          Product current_pro = Product.class.cast(doc.get());          
          //Armar hashmap
          map.put("message", "Id de producto encontrado");
          map.put("product", current_pro);
      }else{
          map.put("message", "Id de producto no encontrado");
      }
      return map;
  }


  //Clase interna para crear transaccion -> depósito (DEPOSIT)
  public HashMap<String, Object> createDeposit(@RequestBody Product product, Double amount, Transaction transaction  ){
      HashMap<String, Object> map = new HashMap<>();
      try{
          log.info("createDeposit:::::");  
          if(product.getProductType().equals("FIXED_TERM_ACCOUNT") && product.getTransactionDate() != null){
            log.info("entrada 1");

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); //yyyy/MM/dd
            Date current_date = new Date();            
            String s_current_date = dateFormat.format(current_date).toString();         


            String transactionDate = product.getTransactionDate(); //new SimpleDateFormat("dd/MM/yyyy").parse(product.getTransactionDate());            
            log.info(s_current_date);
            log.info("entrada 1.1");
            log.info(transactionDate);
            log.info("entrada 1.2");

            if(s_current_date.equals(transactionDate) == false){
              log.info("entrada 2");
              map.put("message", "No se encuentra en la fecha de transacción registrada en la cuenta de plazo fijo.");
            }else{
              log.info("entrada 3.0");
              //Actualizar producto
              Double New_amount = product.getAmount() + amount;
              product.setAmount(New_amount);
              productService.createProduct(product);
              //Crear transacción
              map.put("transaction", transactionService.createTransaction(transaction));
            }

          }else{
            log.info("entrada 3");
            //Actualizar producto
            Double New_amount = product.getAmount() + amount;
            product.setAmount(New_amount);
            productService.createProduct(product);
            //Crear transacción
            map.put("transaction", transactionService.createTransaction(transaction));
          
          }

      } catch(Exception e) {
          e.printStackTrace();
          map.put("message", "error");
      }                    
      return map;
  }

  //Clase interna para crear transaccion -> pago (PAYMENT)
  public HashMap<String, Object> createPayment(@RequestBody Product product, Double amount, Transaction transaction  ){
    HashMap<String, Object> map = new HashMap<>();
    try{

        //Actualizar producto
        Double New_amount = product.getAmount() + amount;
        product.setAmount(New_amount);
        productService.createProduct(product);
        //Crear transacción
        map.put("transaction", transactionService.createTransaction(transaction));

    }catch(Exception e) {
        e.printStackTrace();
        map.put("message", "error");
    }                    
    return map;
  }

  //Clase interna para crear transaccion -> consumo (CONSUMPTION)
  public HashMap<String, Object> createConsumption(@RequestBody Product product, Double amount, Transaction transaction  ){
    HashMap<String, Object> map = new HashMap<>();
    try{
        //Validar el saldo para la transacción
        Double current_amount = product.getAmount(); 
        Double new_amount = current_amount - amount;

        if( new_amount < 0){
          map.put("message", "Saldo insuficiente para la transacción");
        }else{
          product.setAmount(new_amount);
          productService.createProduct(product);
          //Crear transacción
          map.put("transaction", transactionService.createTransaction(transaction));
        }       

    }catch(Exception e) {
        e.printStackTrace();
        map.put("message", "error");
    }                    
    return map;
  }

  //Clase interna para crear transaccion -> retiro (BANK_WHITDRAWALL)
  public HashMap<String, Object> createBankWithdrawall(@RequestBody Product product, Double amount, Transaction transaction  ){
    HashMap<String, Object> map = new HashMap<>();
    try{
        //Validar el saldo para la transacción
        Double current_amount = product.getAmount(); 
        Double new_amount = current_amount - amount;

        if( new_amount < 0){
          map.put("message", "Saldo insuficiente para la transacción");
        }else{

          if(product.getProductType().equals("FIXED_TERM_ACCOUNT") && product.getTransactionDate() != null){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); 
            Date current_date = new Date();            
            String s_current_date = dateFormat.format(current_date).toString();         


            String transactionDate = product.getTransactionDate();           

            if(s_current_date.equals(transactionDate) == false){
              log.info("entrada 2");
              map.put("message", "No se encuentra en la fecha de transacción registrada en la cuenta de plazo fijo.");
            }else{
              product.setAmount(new_amount);
              productService.createProduct(product);
              //Crear transacción
              map.put("transaction", transactionService.createTransaction(transaction));
            }
          }else{
            product.setAmount(new_amount);
            productService.createProduct(product);
            //Crear transacción
            map.put("transaction", transactionService.createTransaction(transaction));
          }

        }       

    }catch(Exception e) {
        e.printStackTrace();
        map.put("message", "error");
    }                    
    return map;
  }


  //Microservicio para crear cuentas
  @PostMapping("createTransaction")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> createTransaction(@RequestBody Transaction new_trans){

      log.info("entrando a método createTransaction");
      Map<String, Object> salida = new HashMap<>();      
      HashMap<String, Object> product_data = validateProduct(new_trans.getIdProduct());  
      String message = (product_data.get("message")).toString();
      

      if(message == "Id de producto no encontrado"){
          log.info("id incorrecto");
          salida.put("message", "Id de producto no encontrado");  
      }else{         
          Product current_product = Product.class.cast(product_data.get("product"));
          log.info("entro al else");
          String transactionType = new_trans.getTransactionType();
          log.info(transactionType);
          
          //Cantidad de transacciones
          List <Transaction> Q_transactions = transRepo.findByIdProduct(new_trans.getIdProduct());
          int q_transactions = Q_transactions.size();
          log.info("cantidad de tran::::: "+ q_transactions);

          //Asignar fecha de creación
          java.util.Date date = new java.util.Date();
          new_trans.setRegisterDate(date);
          //Asignar status
          new_trans.setStatus("ACTIVE");

          if(current_product.getProductType().equals("SAVING_ACCOUNT") &&  current_product.getMaximumTransactionLimit() <= q_transactions)
          {
            salida.put("ouput", "El número de transacciones permitidas para la cuenta fue alcanzada");
          }else{
            if(transactionType.equals("DEPOSIT" )){
              log.info("1");
              HashMap<String, Object> create_trans_a = createDeposit(  current_product, new_trans.getAmount(), new_trans );
              salida.put("ouput", create_trans_a);
            }else if(transactionType.equals("PAYMENT")){
                log.info("2");
                HashMap<String, Object> create_trans_b = createPayment(  current_product, new_trans.getAmount(), new_trans );
                salida.put("ouput", create_trans_b);
            }else if(transactionType.equals("CONSUMPTION")){
                log.info("3");
                HashMap<String, Object> create_trans_c = createConsumption(  current_product, new_trans.getAmount(), new_trans );
                salida.put("ouput", create_trans_c);
            }else if(transactionType.equals("BANK_WHITDRAWALL")){
              log.info("3");
              HashMap<String, Object> create_trans_d = createBankWithdrawall(  current_product, new_trans.getAmount(), new_trans );
              salida.put("ouput", create_trans_d);
            }
          }

      }  
      
      log.info("imprime nomas");
      return ResponseEntity.ok(salida);
  }




}

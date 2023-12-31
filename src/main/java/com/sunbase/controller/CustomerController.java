package com.sunbase.controller;

import com.sunbase.customer.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class CustomerController {

    private String bearerToken = ""; 

    
    private List<Customer> customers = new ArrayList<>();



    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@RequestBody AuthRequest authRequest) {
        
        if (authRequest.getLogin_id().equals("test@sunbasedata.com") && authRequest.getPassword().equals("Test@123")) {
           
            bearerToken = "dummy_bearer_token";
            return ResponseEntity.ok().body(bearerToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/createCustomer")
    public ResponseEntity<String> createCustomer(@RequestHeader("Authorization") String authHeader, @RequestBody Customer customer) {
        if (!isValidAuthorization(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (customer.getFirst_name() == null || customer.getLast_name() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First Name or Last Name is missing");
        }

        
        UUID uuid = UUID.randomUUID();
        customer.setUuid(uuid.toString());

        customers.add(customer);

        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully Created");
    }

    @GetMapping("/getCustomerList")
    public ResponseEntity<List<Customer>> getCustomerList(@RequestHeader("Authorization") String authHeader) {
        if (!isValidAuthorization(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(customers);
    }

    @PostMapping("/updateCustomer")
    public ResponseEntity<String> updateCustomer(@RequestHeader("Authorization") String authHeader, @RequestParam String uuid, @RequestBody Customer customer) {
        if (!isValidAuthorization(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Customer existingCustomer = findCustomerByUuid(uuid);
        if (existingCustomer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UUID not found");
        }

        existingCustomer.setFirst_name(customer.getFirst_name());
        existingCustomer.setLast_name(customer.getLast_name());
        existingCustomer.setStreet(customer.getStreet());
        existingCustomer.setAddress(customer.getAddress());
        existingCustomer.setCity(customer.getCity());
        existingCustomer.setState(customer.getState());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhone(customer.getPhone());

        return ResponseEntity.ok("Successfully Updated");
    }

    @PostMapping("/deleteCustomer")
    public ResponseEntity<String> deleteCustomer(@RequestHeader("Authorization") String authHeader, @RequestParam String uuid) {
        if (!isValidAuthorization(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Customer existingCustomer = findCustomerByUuid(uuid);
        if (existingCustomer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UUID not found");
        }

        customers.remove(existingCustomer);

        return ResponseEntity.ok("Successfully deleted");
    }

   
    private boolean isValidAuthorization(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer " + bearerToken);
    }

    
    private Customer findCustomerByUuid(String uuid) {
        return customers.stream()
                .filter(customer -> customer.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/")
    public String loginPage() {
        return "index";
    }

    @GetMapping("/customer")
    public String customerListPage(Model model) {
        Customer customer = new Customer();
        model.addAttribute("customers", customer);
        return "customer"; 
    }

    @GetMapping("/add-customer")
    public String addCustomerPage() {
        return "add_customer";
    }
}

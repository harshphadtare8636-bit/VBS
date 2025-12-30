package com.vbs.demo.controller;

import com.vbs.demo.dto.TranferDto;
import com.vbs.demo.dto.TransactionDto;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.TransactionRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    TransactionRepo transactionRepo;

     // new object bana ke store kiya value
    @PostMapping("/deposit")
    public String deposit(@RequestBody TransactionDto obj)
    {
        User user = userRepo.findById((obj.getId()))
                .orElseThrow(()->new RuntimeException("Not found"));
        double newBalance = user.getBalance() + obj.getAmount();
        user.setBalance(newBalance);
        userRepo.save(user);
       // store kiya value
        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Rupees"+obj.getAmount()+" Deposit Successful");
        t.setUserId(obj.getId());
        transactionRepo.save(t);
        return "Deposit successfull";

    }

// withdraw
    @PostMapping("/withdraw")
    public String withdraw(@RequestBody Transaction obj)
    {
        User user = userRepo.findById(obj.getId())
                .orElseThrow(() -> new RuntimeException("Not found"));
        double newBalance = user.getBalance() - obj.getAmount();
        if(newBalance<0)
        {
            return "Balance not sufficient";
        }
        user.setBalance(newBalance);

        userRepo.save(user);

        // store kiya value
        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Ruppes" + obj.getAmount() + " Withdrawal Successful");
        t.setUserId(obj.getId());
        transactionRepo.save(t);
        return " Withdrawal Successful";
    }
// transfer
    @PostMapping("/transfer")
    public String transfer(@RequestBody TranferDto obj)
    {
        User sender = userRepo.findById(obj.getId())
                .orElseThrow(()->new RuntimeException("Not found"));
        User rec = userRepo.findByUsername(obj.getUsername());

        if(rec==null){ return "Username not found"; }
        if(obj.getAmount()<1) {return "invalid username"; }
        if(sender.getId() == rec.getId()){return "Self transaction not allowed";}

        double sBalance = sender.getBalance() -obj.getAmount();
        double rBalance = rec.getBalance() + obj.getAmount();
        sender.setBalance(sBalance);
        rec.setBalance(rBalance);

        userRepo.save(sender);
        userRepo.save(rec);

        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();

        t1.setAmount(obj.getAmount());
        t1.setCurrBalance(sBalance);
        t1.setDescription("Rs "+obj.getAmount()+" Sent to "+obj.getUsername());
        t1.setUserId(obj.getId());

        t2.setAmount(obj.getAmount());
        t2.setCurrBalance(rBalance);
        t2.setDescription("Rs "+obj.getAmount()+" Received from "+sender.getUsername());
        t2.setUserId(rec.getId());

        transactionRepo.save(t1);
        transactionRepo.save(t2);
        return "Transfer Succesfully";

    }
 // passbook
    @GetMapping("/passbook/{id}")
    public List<Transaction> getPassbook(@PathVariable int id)
    {
        return transactionRepo.findAllByUserId(id);
    }
    }

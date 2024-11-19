package com.example.account_management.controllers;

import com.example.account_management.entities.Compte;
import com.example.account_management.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banque")
public class CompteController {

    @Autowired
    private CompteRepository compteRepository;

    /*@GetMapping("/comptes")
    public List<Compte> getComptes(){
        return compteRepository.findAll();
    }*/

    @GetMapping(value = "/comptes", produces = {"application/json","application/xml"})
    public List<Compte> getAllComptes(){
        return compteRepository.findAll();
    }

    @GetMapping(value = "/comptes/{id}", produces = {"application/json","application/xml"})
    public ResponseEntity<Compte> getComptesById(@PathVariable long id){
        return compteRepository.findById(id).map(compte -> ResponseEntity.ok().body(compte)).orElse(ResponseEntity.notFound().build());
    }

    /*@PostMapping("/comptes")
    public Compte createCompte(@RequestBody Compte compte){
        return compteRepository.save(compte);
    }*/

    @PostMapping(value = "/comptes", consumes = {"application/json","application/xml"}, produces = {"application/json","application/xml"})
    public Compte createCompte(@RequestBody Compte compte){
        return compteRepository.save(compte);
    }

    /*@PutMapping("/comptes/{id}")
    public ResponseEntity<Compte> updateCompte(@PathVariable Long id, @RequestBody Compte compteDetails){
        return compteRepository.findById(id).map(compte -> {
            compte.setSolde(compteDetails.getSolde());
            compte.setType(compteDetails.getType());
            Compte updatedCompte = compteRepository.save(compte);
            return ResponseEntity.ok(updatedCompte);
        }).orElse(ResponseEntity.notFound().build());
    }*/

    @PutMapping(value = "/comptes/{id}", consumes = {"application/json","application/xml"}, produces = {"application/json","application/xml"})
    public ResponseEntity<Compte> updateCompte(@PathVariable Long id, @RequestBody Compte compteDetails){
        return compteRepository.findById(id).map(compte -> {
            compte.setSolde(compteDetails.getSolde());
            compte.setType(compteDetails.getType());
            Compte updatedCompte = compteRepository.save(compte);
            return ResponseEntity.ok().body(updatedCompte);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/comptes/{id}")
    public ResponseEntity<Void> deleteCompte(@PathVariable Long id){
        return compteRepository.findById(id).map(compte -> {
            compteRepository.delete(compte);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
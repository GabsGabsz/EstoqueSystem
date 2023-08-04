package com.example.springboot.controllers;

import com.example.springboot.dtos.ProdutoRecordDto;
import com.example.springboot.models.produtoModel;
import com.example.springboot.repositories.RepositorioProduto;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class controllerProduto {

    @Autowired
    RepositorioProduto RepositorioProduto;

    @PostMapping("/produtos")
    public ResponseEntity<produtoModel> saveProduto(@RequestBody @Valid ProdutoRecordDto ProdutoRecordDto){
        var produtoModel = new produtoModel();
        BeanUtils.copyProperties(ProdutoRecordDto,produtoModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(RepositorioProduto.save(produtoModel));
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<produtoModel>> getallprodutos(){
        List<produtoModel> listaProdutos = RepositorioProduto.findAll();
        if(!listaProdutos.isEmpty()){
            for(produtoModel produto : listaProdutos){
                UUID id = produto.getIdProduto();
                produto.add(linkTo(methodOn(controllerProduto.class).getOneProduto(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(listaProdutos);
    }

    @GetMapping("/produtos/{id}")
    public ResponseEntity<Object> getOneProduto(@PathVariable(value = "id")UUID id){
        Optional<produtoModel> produto = RepositorioProduto.findById(id);
        if(produto.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        produto.get().add(linkTo(methodOn(controllerProduto.class).getallprodutos()).withSelfRel());
         return ResponseEntity.status(HttpStatus.OK).body(produto.get());
    }

    @PutMapping("/produtos/{id}")
    public ResponseEntity<Object> updateProduto(@PathVariable(value = "id")UUID id,@RequestBody @Valid ProdutoRecordDto produtoRecorDto){
        Optional<produtoModel> produto = RepositorioProduto.findById(id);
        if(produto.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        var produtoModel = produto.get();
        BeanUtils.copyProperties(produtoRecorDto,produtoModel);
        return ResponseEntity.status(HttpStatus.OK).body(RepositorioProduto.save(produtoModel));
    }

    @DeleteMapping("/produtos/{id}")
    public ResponseEntity<Object> deleteProduto(@PathVariable(value = "id")UUID id){
        Optional<produtoModel> produto = RepositorioProduto.findById(id);
        if(produto.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        RepositorioProduto.delete(produto.get());
        return ResponseEntity.status(HttpStatus.OK).body("Produto deletado com sucesso");
    }

}

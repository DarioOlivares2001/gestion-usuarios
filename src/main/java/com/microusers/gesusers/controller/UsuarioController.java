package com.microusers.gesusers.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.microusers.gesusers.exception.ResourceNotFoundException;
import com.microusers.gesusers.model.Usuario;
import com.microusers.gesusers.service.UsuarioService;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@Validated
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        List<EntityModel<Usuario>> usuarioResources = usuarios.stream()
            .map(usuario -> EntityModel.of(usuario,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).obtenerUsuario(usuario.getId())).withSelfRel()
            ))
            .collect(Collectors.toList());

        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).listarUsuarios());
        CollectionModel<EntityModel<Usuario>> resources = CollectionModel.of(usuarioResources, linkTo.withRel("usuarios"));

        return resources;
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuario(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con ID " + id + " no fue encontrado."));
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.obtenerUsuario(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con ID " + id + " no fue encontrado."));
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario detallesUsuario) {
        Usuario usuario = usuarioService.obtenerUsuario(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con ID " + id + " no fue encontrado."));

        usuario.setUsername(detallesUsuario.getUsername());
        usuario.setPassword(detallesUsuario.getPassword());
        usuario.setRol(detallesUsuario.getRol());

        Usuario usuarioActualizado = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @GetMapping(params = "username")
    public ResponseEntity<Usuario> obtenerUsuarioPorNombre(@RequestParam String username) {
        Usuario usuario = usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con username " + username + " no fue encontrado."));
        return ResponseEntity.ok(usuario);
    }
}

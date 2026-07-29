package com.fabio.perfumeshop_api.order.internal;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
class CartController {

    private final CartService cartService;

    //Método para ver el carrito del usuario autenticado.
    @GetMapping
    CartResponse getCart(){
    return cartService.getCart();
    }

    //Añadir un item al carrito.
    @PostMapping("/items")
    CartResponse addItem(@Valid @RequestBody AddItemRequest request){
        return cartService.addItem(request.perfumeId(), request.quantity());
    }

    //Modificar la cantidad de un item existente.
    @PutMapping("/items/{perfumeId}")
    CartResponse updateItem(@PathVariable Long perfumeId,
                            @Valid @RequestBody UpdateItemRequest request){
        return cartService.updateItem(perfumeId, request.quantity());
    }

    //Quitar un item del carrito
    @DeleteMapping("/items/{perfumeId}")
    CartResponse removeItem(@PathVariable Long perfumeId){
        return cartService.removeItem(perfumeId);
    }

    //Vaciar el carrito entero
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void clearCart(){
        cartService.clearCart();

    }





}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.polstat.perpustakaan.rpc;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Dwinanda
 */
@Getter
@Setter
public class JsonRpcResponse {
private String jsonrpc;
private Object result;
private Object error;
private String id;
//tambahkan method getter dan setter

    public JsonRpcResponse(Object result, String id) {
        this.jsonrpc = "2.0";  // JSON-RPC version
        this.result = result;
        this.error = null;  // Assuming no error when result is set
        this.id = id;
    }

}

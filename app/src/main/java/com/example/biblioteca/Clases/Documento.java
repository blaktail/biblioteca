package com.example.biblioteca.Clases;

public class Documento {
    String Nombre,Id_doc,Id_usuario,path_file;

    public String getId_doc() {
        return Id_doc;
    }

    public void setId_doc(String id_doc) { Id_doc = id_doc; }

    public String getId_usuario() {
        return Id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        Id_usuario = id_usuario;
    }

    public String getPath_file() {
        return path_file;
    }

    public void setPath_file(String path_file) {
        this.path_file = path_file;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }
}

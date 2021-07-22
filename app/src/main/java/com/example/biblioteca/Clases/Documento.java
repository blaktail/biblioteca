package com.example.biblioteca.Clases;


public class Documento {

    String Nombre,Img_pdf,  Id_usuario,  url,fecha;
    public Documento(){}

    public Documento(String nombre, String img_pdf, String id_usuario, String url,String fecha) {
        this.Nombre = nombre;
        this.Img_pdf = img_pdf;
        this.Id_usuario = id_usuario;
        this.url = url;
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    //getters and setters
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getImg_pdf() {
        return Img_pdf;
    }
    public void setImg_pdf(String img_pdf) { Img_pdf = img_pdf; }
    public String getId_usuario() {
        return Id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        Id_usuario = id_usuario;
    }
    public String getNombre() {
        return Nombre;
    }
    public void setNombre(String nombre) {
        Nombre = nombre;
    }





}
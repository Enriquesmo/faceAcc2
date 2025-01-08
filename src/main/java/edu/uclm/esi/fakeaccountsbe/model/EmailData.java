package edu.uclm.esi.fakeaccountsbe.model;

public class EmailData {
    private String destinatario;
    private String asunto;
    private String cuerpo;

  
    public EmailData(String destinatario, String asunto, String cuerpo) {
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }
}

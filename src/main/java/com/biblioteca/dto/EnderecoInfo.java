package com.biblioteca.dto;

/**
 * DTO de resposta da API ViaCEP.
 * Contém os campos preenchidos automaticamente no cadastro (RF-10).
 */
public class EnderecoInfo {

    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;

    public EnderecoInfo() {
    }

    public EnderecoInfo(String logradouro, String bairro, String localidade, String uf) {
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.localidade = localidade;
        this.uf = uf;
    }

    // --- Getters e Setters ---

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}

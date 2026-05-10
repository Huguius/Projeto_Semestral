/**
 * Biblioteca Pessoal — JavaScript
 * Auto-preenchimento de endereço via CEP (RF-10 seção 9).
 */
document.addEventListener('DOMContentLoaded', function() {
    const cepInput = document.getElementById('cep');

    if (cepInput) {
        cepInput.addEventListener('blur', async function() {
            const cep = this.value.replace(/\D/g, '');
            if (cep.length !== 8) return;

            try {
                const response = await fetch('/api/cep/' + cep);
                if (response.ok) {
                    const data = await response.json();
                    document.getElementById('logradouro').value = data.logradouro || '';
                    document.getElementById('bairro').value = data.bairro || '';
                    document.getElementById('cidade').value = data.localidade || '';
                    document.getElementById('uf').value = data.uf || '';
                } else if (response.status === 404) {
                    alert('CEP não encontrado. Preencha o endereço manualmente.');
                } else {
                    alert('Erro ao buscar CEP. Preencha manualmente.');
                }
            } catch (error) {
                alert('Serviço indisponível. Preencha o endereço manualmente.');
            }
        });
    }
});

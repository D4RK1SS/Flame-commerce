(function () {
    'use strict';

    // Menu mobile
    document.querySelectorAll('[data-nav-toggle]').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var header = btn.closest('.site-header');
            var aberto = header.classList.toggle('open');
            btn.setAttribute('aria-expanded', aberto ? 'true' : 'false');
        });
    });

    // Alertas: botão fechar + sumir sozinho (sucesso)
    document.querySelectorAll('.alert').forEach(function (alerta) {
        var fechar = alerta.querySelector('.close');
        if (fechar) {
            fechar.addEventListener('click', function () { alerta.remove(); });
        }
        if (alerta.classList.contains('alert-success') && !alerta.classList.contains('inline')) {
            setTimeout(function () { alerta.remove(); }, 5000);
        }
    });

    // Mostrar/ocultar senha
    document.querySelectorAll('.toggle-pass').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var campo = document.getElementById(btn.dataset.target);
            var mostrar = campo.type === 'password';
            campo.type = mostrar ? 'text' : 'password';
            btn.textContent = mostrar ? 'Ocultar' : 'Mostrar';
        });
    });

    // Stepper de quantidade (+ / -)
    document.querySelectorAll('.qty').forEach(function (qty) {
        var input = qty.querySelector('input');
        qty.querySelectorAll('button').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var passo = parseInt(btn.dataset.step, 10);
                var min = parseInt(input.min || '1', 10);
                var max = input.max ? parseInt(input.max, 10) : Infinity;
                var valor = (parseInt(input.value, 10) || min) + passo;
                input.value = Math.min(Math.max(valor, min), max);
            });
        });
    });

    // Checkout: mostra campos do cartão só quando "Cartão" estiver selecionado
    var cartaoBox = document.getElementById('card-fields');
    if (cartaoBox) {
        var atualizar = function () {
            var selecionado = document.querySelector('input[name="metodo"]:checked');
            cartaoBox.hidden = !(selecionado && selecionado.value === 'CARTAO');
        };
        document.querySelectorAll('input[name="metodo"]').forEach(function (r) {
            r.addEventListener('change', atualizar);
        });
        atualizar();
    }

    // Máscaras simples
    function mascara(id, fn) {
        var el = document.getElementById(id);
        if (el) { el.addEventListener('input', function () { el.value = fn(el.value); }); }
    }
    mascara('numeroCartao', function (v) {
        return v.replace(/\D/g, '').slice(0, 19).replace(/(\d{4})(?=\d)/g, '$1 ');
    });
    mascara('validade', function (v) {
        var d = v.replace(/\D/g, '').slice(0, 4);
        return d.length > 2 ? d.slice(0, 2) + '/' + d.slice(2) : d;
    });
    mascara('cvv', function (v) { return v.replace(/\D/g, '').slice(0, 4); });
    mascara('cep', function (v) {
        var d = v.replace(/\D/g, '').slice(0, 8);
        return d.length > 5 ? d.slice(0, 5) + '-' + d.slice(5) : d;
    });
    mascara('estado', function (v) { return v.replace(/[^a-zA-Z]/g, '').slice(0, 2).toUpperCase(); });

    // Confirmação antes de ações destrutivas
    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            if (!window.confirm(form.dataset.confirm)) { e.preventDefault(); }
        });
    });
})();

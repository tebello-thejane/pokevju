(function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
const urlDomain = BACKENDDOMAIN;
console.log(urlDomain)


window.addEventListener('DOMContentLoaded', event => {

    var justNames = [];

    $.getJSON(`${urlDomain}/api/v1/allnames`, function(data) {
        $.each(data, function(index, value) {
            justNames.push(value.name.toUpperCase());
            $('#poke-names').append(`<option value='${value.name}'>`);
        });
    });

    $("#poke-names-inp").on("input", function() {
        if (justNames.includes($("#poke-names-inp")[0].value.toUpperCase())) {
            const val = $("#poke-names-inp")[0].value;
            const opts = $('#poke-names')[0].childNodes;
            for (var i = 1; i < opts.length; i++) {
                if (opts[i].value.toUpperCase() === val.toUpperCase()) {
                    const name = opts[i].value;

                    $("#heading")[0].innerText = name;
                    $("#poke-img")[0].src = `${urlDomain}/api/v1/sprite/${name.toLowerCase()}`;
                    break;
                }
            }
        }
    });
});

},{}]},{},[1]);

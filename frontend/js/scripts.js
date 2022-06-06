import { urlDomain } from "./config.js";

window.addEventListener('DOMContentLoaded', event => {

    var justNames = [];

    $.getJSON(`${urlDomain}/api/allnames`, function(data) {
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
                    $("#poke-img")[0].src = `${urlDomain}/api/sprite/${name.toLowerCase()}`;
                    break;
                }
            }
        }
    });
});

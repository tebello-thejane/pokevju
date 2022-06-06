/*!
* Start Bootstrap - Simple Sidebar v6.0.5 (https://startbootstrap.com/template/simple-sidebar)
* Copyright 2013-2022 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-simple-sidebar/blob/master/LICENSE)
*/
//
// Scripts
//

import { urlDomain } from "./config.js";

window.addEventListener('DOMContentLoaded', event => {

    var names;
    var justNames = [];

    var urlD = urlDomain; //TODO: Why do we need to do this??

    $.getJSON(urlD + '/api/allnames', function(data) {
        names = data;

        $.each(data, function(index, value) {
            justNames.push(value.name.toUpperCase());
            $('#poke-names').append("<option value='" + value.name + "''>");
        });
    });

    $("#poke-names-inp").on("input", function() {
        if (justNames.includes($("#poke-names-inp")[0].value.toUpperCase())) {
            var val = $("#poke-names-inp")[0].value;
            var opts = $('#poke-names')[0].childNodes;
            for (var i = 1; i < opts.length; i++) {
                if (opts[i].value.toUpperCase() === val.toUpperCase()) {
                    var name = opts[i].value;
                    $("#heading")[0].innerText = name;
                    $("#poke-img")[0].src = urlD + "/api/sprite/" + name.toLowerCase();
                    $("#poke-img")[0].width = 500;
                    $("#poke-img")[0].height = 500;
                    break;
                }
            }
        }
    });

});

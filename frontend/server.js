var express = require('express');
require('dotenv').config();
var app = express();

//setting middleware
app.use(express.static(__dirname )); //Serves resources from public folder


var server = app.listen(process.env.PORT || 5000);

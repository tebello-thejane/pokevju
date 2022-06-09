var express = require('express');
require('dotenv').config();
var app = express();

app.set('view engine', 'ejs');
//setting middleware

app.get('/', (req, res) => {
  res.render('index', { BACKEND: process.env.BACKENDDOMAIN || "http://localhost:8080"})
})

app.use(express.static(__dirname )); //Serves resources from public folder


var server = app.listen(process.env.PORT || 8081);

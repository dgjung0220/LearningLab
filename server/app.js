const express = require('express')
const app = express()
const mongoose = require('mongoose')

var port = process.env.PORT || 3000;

// DB
var db = mongoose.connection
db.on('error', console.error)
db.once('open', function() {
    console.log('Connected to mongod server')
})
mongoose.connect('mongodb://localhost:27017/testdb')

var LocationVo = require('./models/locationVo')

var router = require('./routes')(app, LocationVo)
var server = app.listen(port, function(){
    console.log("Express server has started on port " + port)
});
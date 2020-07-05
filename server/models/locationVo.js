var mongoose = require('mongoose')
var Schema = mongoose.Schema


// https://mongoosejs.com/docs/schematypes.html#schematypes
var locationSchema = new Schema({

    index: String,
    provider: String,
    userTime : Date,
    sysTime : Number,
    latitude : Number,
    longitude : Number,
    altitude : Number,
    bearing : Number,
    accuracy : Number,
    speed : Number
})

module.exports = mongoose.model('locationVo', locationSchema)
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var heroSchema = new Schema({
    name: {
        type: String,
        required: true
    },
    desc: {
        type: String,
        required: true
    },
    image: {
        type: String,
        default: ''
    }
}, {
        timestamps: true
    });

var Heroes = mongoose.model('Hero', heroSchema);
module.exports = Heroes;
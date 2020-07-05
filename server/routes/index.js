//https://velopert.com/594
var moment = require('moment')

module.exports = function(app, LocationVo) {

    var indexTime = ''

    app.post('/post', (req, res) => {

        var inputSensorData

        req.on('data', (data) => {
            inputSensorData = JSON.parse(data)
        })

        req.on('end', () => {
            var type = inputSensorData.type
            var data = inputSensorData.data.split(',')

            switch (type){

                case 'location' :

                    console.log(data.join())

                    var locationVo = new LocationVo()

                    locationVo.index = indexTime
                    locationVo.provider = data[0]
                    locationVo.userTime = data[1]
                    locationVo.sysTime = data[2]
                    locationVo.latitude = data[3]
                    locationVo.longitude = data[4]
                    locationVo.altitude = data[5]
                    locationVo.bearing = data[6]
                    locationVo.accuracy = data[7]
                    locationVo.speed = data[8]

                    locationVo.save(function(err) {
                        if (err) {
                            console.error(err)
                            return
                        }
                    })

                    break
                
                case 'SOF':
                    indexTime = moment().format()
                    break
                case 'EOF':
                    index = ''
                    console.log('---------------------------------------\n')
                    break

                default:
                    break
            }
        })

        res.write('')
        res.end()
    })

}
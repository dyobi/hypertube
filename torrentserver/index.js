const express = require('express');
const cors = require('cors');
const app = express();
const TorrentService = require('./src');
const fs = require('fs');
const https = require('https');
const privateKey = fs.readFileSync('cert/key.pem', 'utf8');
const certificate = fs.readFileSync('cert/hypertube.pem', 'utf8');
const credentials = { key: privateKey, cert: certificate };
const server = https.createServer(credentials, app);

const STREAM_PORT = 8445;

app.use(cors());

const client = new TorrentService();

client.on('error', err => {
    console.log(err.message);
});

app.get('/stream/add/:magnet', (req, res) => {

    const magnet = req.params.magnet;

    const tor = client.get(magnet);

    if (tor != null) {
        let max = {
            name: '',
            length: 0
        };
        for (i = 0; i < tor.files.length; i++) {
            if (max.length < tor.files[i].length)
                max = {
                    name: tor.files[i].name,
                    length: tor.files[i].length
                };
        } res.json(max);
    }

    client.add(magnet, torrent => {
        console.log(`magnet(${magnet}) has added`);
        let max = {
            name: '',
            length: 0
        };

        torrent.files.forEach(data => {
            if (max.length < data.length)
                max = {
                    name: data.name,
                    length: data.length
                };
        });
        res.json(max);
    });
});

app.get('/stream/play/:magnet/:filename', (req, res, next) => {
    const magnet = req.params.magnet;
    const filename = req.params.filename;

    console.log(filename);

    const tor = client.get(magnet);

    let file = {};

    for (i = 0; i < tor.files.length; i++) {
        if (tor.files[i].name == filename) {
            file = tor.files[i];
        }
    }

    const range = req.headers.range;

    if (!range) {
        let err = new Error('Wrong range');
        err.status = 416;
        return next(err);
    }

    const file_size = file.length;
    const positions = range.replace(/bytes=/, '').split('-');
    const start = parseInt(positions[0], 10);
    const end = positions[1] ? parseInt(positions[1], 10) : file_size - 1;

    const chunksize = end - start + 1;

    res.writeHead(206, {
        'Content-Range': 'bytes ' + start + '-' + end + '/' + file_size,
        'Accept-Ranges': 'bytes',
        'Content-Length': chunksize,
        'Content-Type': 'video/mp4'
    });

    let stream = file.createReadStream({
        start,
        end
    });

    if (filename.split('.')[filename.split('.').length - 1] === 'mkv') {
        
    } else {
        stream.pipe(res);
    }

    stream.on('error', err => {
        return next(err);
    });
});

server.listen(STREAM_PORT, () => console.log(`Torrent server is listening on ${STREAM_PORT}`));
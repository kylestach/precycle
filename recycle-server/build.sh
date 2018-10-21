#!/usr/bin/env bash

cp ../ImageSearch/predict.py ./server/predict.py

docker build -t recycle-server .



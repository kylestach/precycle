FROM alpine:latest
MAINTAINER Will Gulian <will@willgulian.com>

RUN apk add --no-cache python3 python3-dev musl-dev gcc g++

RUN mkdir -p /opt/recycle-server
WORKDIR /opt/recycle-server

RUN pip3 install -U pip gunicorn

COPY requirements.txt .
RUN pip3 install -r requirements.txt

COPY server/ ./server/

EXPOSE 8000

CMD ["gunicorn", "-b", "0.0.0.0:8000", "server.main:app"]


FROM python:3-alpine
MAINTAINER Will Gulian <will@willgulian.com>

RUN mkdir -p /opt/recycle-server
WORKDIR /opt/recycle-server

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY server/ ./server/


CMD ["python", "-m", "server.main"]


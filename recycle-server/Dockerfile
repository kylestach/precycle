FROM python:3-alpine
MAINTAINER Will Gulian <will@willgulian.com>

RUN mkdir -p /opt/recycle-server
WORKDIR /opt/recycle-server

RUN pip install gunicorn

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY server/ ./server/

EXPOSE 8000

CMD ["gunicorn", "-b", "0.0.0.0:8000", "server.main:app"]


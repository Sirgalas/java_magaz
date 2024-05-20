include ./.env

init: down build up

up:
	docker-compose up -d

build:
	docker-compose build

down:
	docker-compose down --remove-orphans
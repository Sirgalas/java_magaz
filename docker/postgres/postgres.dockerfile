ARG POSTGRES_VERSION=13.5-alpine
FROM postgres:${POSTGRES_VERSION}

#RUN mkdir -p "/pgdata/"

#RUN cp -R /var/lib/postgresql/data/ /pgdata/

CMD ["postgres"]

EXPOSE 5432
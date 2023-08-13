FROM minio/minio:RELEASE.2023-08-09T23-30-22Z
WORKDIR data
COPY data .
EXPOSE '9000'
EXPOSE '9001'
ENV MINIO_ROOT_USER='admin'
ENV MINIO_ROOT_PASSWORD='Admin@123'
CMD ["mkdir", "-p", "prj-doc"]
CMD ["server", "--console-address", ":9001", "/data"]

# How to build and push the promoter image

```
docker build -t quay.io/quarkusqeteam/quarkus-apps-catalog-promoter .
docker login quay.io
docker push quay.io/quarkusqeteam/quarkus-apps-catalog-promoter
```
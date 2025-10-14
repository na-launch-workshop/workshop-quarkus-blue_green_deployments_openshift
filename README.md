# Hello Country Service (Quarkus)

Quarkus REST application that mirrors the Node.js workshop service. It loads `src/main/resources/data/greetings.json`, reads the `COUNTRY_CODE` environment variable (default `EN`), and exposes a single `GET /` endpoint returning:

```json
{
  "code": "EN",
  "message": "Hello World"
}
```

If the configured country code is unknown, the endpoint responds with `404` and `{ "error": "Unknown country code '<CODE>'" }`.

## Local Development

Start Quarkus dev mode:

```bash
./mvnw quarkus:dev
```

Override the greeting before launching dev mode (value is upper-cased automatically):

```bash
COUNTRY_CODE=FR ./mvnw quarkus:dev
```

Quarkus listens on `http://localhost:8080` by default, or use `PORT=<value>` to override. Test the endpoint:

```bash
curl http://localhost:8080/
```

Run the tests:

```bash
./mvnw test
```

## Container Build

Build a container image using the provided multi-stage Dockerfile (replace `<REGISTRY>/<REPOSITORY>` with your registry path):

```bash
IMAGE=<REGISTRY>/<REPOSITORY>/hello-country-service:latest
podman build -t "$IMAGE" .
# or: docker build -t "$IMAGE" .

podman push "$IMAGE"
# or: docker push "$IMAGE"
```

The resulting image runs on UBI OpenJDK 21 and exposes port 8080.

### OpenShift Local Registry (external route)

```bash
oc project <NAMESPACE>
REGISTRY=$(oc registry info)
podman login -u "$(oc whoami)" -p "$(oc whoami -t)" "$REGISTRY"
IMAGE=${REGISTRY}/<NAMESPACE>/hello-country-service:latest
podman build -t "$IMAGE" .
podman push "$IMAGE"
```

### Build Inside OpenShift (BuildConfig)

```bash
oc project <NAMESPACE>
oc new-build --strategy=docker --binary --name hello-country-service
oc start-build hello-country-service --from-dir=. --follow
oc get is hello-country-service -n <NAMESPACE>
```

## Deploy to Knative

Update `knative-service.yaml` with your image (set `<NAMESPACE>` accordingly) and apply it:

```bash
oc apply -f knative-service.yaml -n <NAMESPACE>
```

Knative injects the `PORT` environment variable automatically; adjust the greeting via `COUNTRY_CODE` in the manifest. Retrieve the URL and test once ready:

```bash
kn service describe hello-country-service -o url
curl "$(kn service describe hello-country-service -o url)"
```

To change the greeting later:

```bash
kn service update hello-country-service --env COUNTRY_CODE=FR
```

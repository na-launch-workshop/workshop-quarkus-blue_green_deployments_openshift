# 🚀 **Module: Quarkus Hello Country Service**

**Technology Stack:**

- Knative
- Quarkus

---

## 🎯 **Scenario**

Quarkus REST application that mirrors the Node.js workshop service. It loads `src/main/resources/data/greetings.json`, reads the `COUNTRY_CODE` environment variable (default `EN`), and exposes a single `GET /` endpoint returning:

```json
{
  "code": "EN",
  "message": "Hello World"
}
```

If the configured country code is unknown, the endpoint responds with `404` and `{ "error": "Unknown country code '<CODE>'" }`.

---

## ✏️  **Local Development**

1. Start Quarkus dev mode:

```bash
./mvnw quarkus:dev
```

2. Override the greeting before launching dev mode (value is upper-cased automatically):

```bash
COUNTRY_CODE=FR ./mvnw quarkus:dev
```

3. Quarkus listens on `http://localhost:8080` by default, or use `PORT=<value>` to override. Test the endpoint:

```bash
curl http://localhost:8080/
```

4. Run the tests:

```bash
./mvnw test
```

---

## 🔨 **Container Build**

Select the option that is most appropriate:

### Generic Registry

Build a container image using the provided multi-stage Dockerfile (replace `<REGISTRY>/<REPOSITORY>` with your registry path):

```bash
IMAGE=<REGISTRY>/<REPOSITORY>/hello-country-service:latest
podman build -t "$IMAGE" .
# or: docker build -t "$IMAGE" .

podman push "$IMAGE"
# or: docker push "$IMAGE"
```

The resulting image runs on UBI OpenJDK 21 and exposes port 8080.

---

### OpenShift Local Registry (external route)

```bash
oc project <NAMESPACE>
REGISTRY=$(oc registry info)
podman login -u "$(oc whoami)" -p "$(oc whoami -t)" "$REGISTRY"
IMAGE=${REGISTRY}/<NAMESPACE>/hello-country-service:latest
podman build -t "$IMAGE" .
podman push "$IMAGE"
```

---

### Build Inside OpenShift (BuildConfig)


```bash
# Point to the project that should own the image
oc project <NAMESPACE>

# Create a Docker strategy BuildConfig (run once)
oc new-build --strategy=docker --binary --name hello-country-service

# Start a build using the local working tree and stream logs
oc start-build hello-country-service --from-dir=. --follow

# Verify the resulting image stream tag
oc get is hello-country-service -n <NAMESPACE>
```

The successful build publishes the image at:
```
image-registry.openshift-image-registry.svc:5000/<NAMESPACE>/hello-country-service:latest
```

---

## 🚢 **Deploy to Knative**

1. Update `knative-service.yaml` with your image reference (for OpenShift local registry, set `your-namespace` accordingly), then apply it:

```bash
oc apply -f knative-service.yaml -n <NAMESPACE>
```

2. Knative injects a `PORT` environment variable automatically; do not set one in the manifest. Override the greeting via `COUNTRY_CODE`.

Or, using the Knative CLI without modifying the YAML:

```bash
IMAGE=image-registry.openshift-image-registry.svc:5000/<NAMESPACE>/hello-country-service:latest

kn service apply hello-country-service \
  --image "$IMAGE" \
  --env COUNTRY_CODE=EN
```

3. Retrieve the URL:

```bash
kn service describe hello-country-service -o url
```

4. Test the service once it is ready:

```bash
curl "$(kn service describe hello-country-service -o url)"
```

5. To update the greeting later, patch the service:

```bash
kn service update hello-country-service --env COUNTRY_CODE=FR
```

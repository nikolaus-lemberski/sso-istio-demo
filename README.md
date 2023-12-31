# Keycloak + Service Mesh

Simple demo to secure a "hello" service with Keycloak and Service Mesh. Not for production!!

## Keycloak installation

See also: https://www.keycloak.org/getting-started/getting-started-openshift

### Installation

Create namespace / project "keycloak" and then install:

```bash
oc process -f https://raw.githubusercontent.com/keycloak/keycloak-quickstarts/latest/openshift/keycloak.yaml \
    -p KEYCLOAK_ADMIN=admin \
    -p KEYCLOAK_ADMIN_PASSWORD=admin \
    -p NAMESPACE=keycloak \
| oc create -f -
```

Find the Keycloak URLs when all pods are ready:

```bash
KEYCLOAK_URL=https://$(oc get route keycloak --template='{{ .spec.host }}') &&
echo "" &&
echo "Keycloak:                 $KEYCLOAK_URL" &&
echo "Keycloak Admin Console:   $KEYCLOAK_URL/admin" &&
echo "Keycloak Account Console: $KEYCLOAK_URL/realms/myrealm/account" &&
echo ""
```

### Configuration

In the Keycloak admin console:

1. Create a realm "myrealm"
2. Create a user "myuser" with first name and last name and set a password 'test' or anything you like better; set "Temporary to "Off"
3. Create a client "myclient" with client type "OpenID Connect", client authentication "On", authentication flow: Standard flow, Direct access grants

In "Access settings", set the root URL to your Keycloak URL (https://keycloak-keycloak.apps-crc.testing in OpenShift local).

In "Advanced", set "Authentication flow overrides" to  
* Browser Flow: browser
* Direct Grant Flow: direct grant

## Service Mesh installation

See also: https://docs.openshift.com/container-platform/4.13/service_mesh/v2x/installing-ossm.html

* ElasticSearch Operator not required for testing and Jaeger settings "in memmory"
* The Red Hat OpenShift distributed tracing platform is installed in the openshift-distributed-tracing namespace and is available for all namespaces in the cluster.
* The Kiali and Red Hat OpenShift Service Mesh Operators are installed in the openshift-operators namespace and are available for all namespaces in the cluster.

Then create ServiceMeshControlPlane (MultiTenant in namespace istio-system) and ServiceMeshMemberRoll and add your apps project / namespace to the member list.

## Project installation

Run in the apps project / namespace:

```bash
oc create -f deployment.yml
oc create -f gateway.yml
```

Find out the route to the Istio Ingress Gateway (`oc get route istio-ingressgateway -n istio-system`) and check, if the projects responds an "Hello" if you call the Gateway on the `/hello` path. If yes, add authentication and authorization via

```bash
oc create -f auth.yml
```

Call the hello URL again you should get an "Unauthorized".

### Login

Get the JWT from Keycloak:

```bash
curl --insecure -L -X POST 'https://keycloak-keycloak.apps-crc.testing/realms/myrealm/protocol/openid-connect/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=myclient' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_secret=<clientsecret>' \
--data-urlencode 'scope=openid' \
--data-urlencode 'username=myuser' \
--data-urlencode 'password=test'
```

Then try again the hello URL with the access token Bearer:

```bash
curl -H "Authorization: Bearer <token>" istio-ingressgateway-istio-system.apps-crc.testing/hello
```

Now the request is routed to the backend service and the app responds with the contents of the JWT.

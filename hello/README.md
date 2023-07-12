# Service A

Simple "hello" service in Python 3 asgi with uvicorn server.

Endpoints:

* "/"
* "/health"

## Develop

```bash
python3 -m venv .venv  
source .venv/bin/activate  
```

In VSCode: Cmd-Shift-P > Select Python Interpreter > .venv/...

### Install dependencies

```bash
pip install -r requirements.txt  
pip install -r requirements_dev.txt
```

### Container image

Use provided Dockerfile to create a container image.
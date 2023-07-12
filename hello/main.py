import json
import os

import jwt

port = int(os.environ.get("PORT", default=8080))


async def app(scope, receive, send):
    assert scope["type"] == "http"

    match scope["path"]:
        case "/":
            text, status = await index(scope["headers"])
        case _:
            text, status = await page_not_found()

    response_headers = [(b"content-type", b"text/plain; charset=utf-8")]
    await send({"type": "http.response.start", "status": status, "headers": response_headers})
    await send({"type": "http.response.body", "body": text.encode("UTF-8")})


async def index(headers):
    key = "authorization"
    auth_header = {k.decode("UTF-8"): v.decode("UTF-8")
                   for (k, v) in headers if k.decode("UTF-8") == key}

    if not auth_header:
        return "Hello!\n\nNo access token provided.", 200

    token = auth_header[key].split(' ')[1]
    decoded = jwt.decode(token, options={"verify_signature": False})

    return f"Hello!\n\n {json.dumps(decoded, indent=2, default=str)}\n", 200


async def page_not_found():
    return "Page not found\n", 404


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=port, proxy_headers=True,
                server_header=False, access_log=False)

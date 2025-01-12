# http_request_tool.py

import requests
import json

# URL 및 설정
login_url = "http://api-gateway:8080/api/user/security/login"
order_url = "http://api-gateway:8080/api/order/products"

# 응답 데이터를 저장할 리스트
login_responses = []

# 1. 로그인 요청 (1만 번 반복)
for i in range(1, 1000):
    login_body = {
        "email": str(i),
        "password": str(1234)
    }
    try:
        response = requests.post(login_url, json=login_body)
        print(f"token : {response.text.strip()}")
        if response.status_code == 200:
            # 토큰 저장
            login_responses.append({"token": response.text.strip()})
        else:
            login_responses.append({"error": f"Failed at iteration {i}: {response.status_code}"})
    except Exception as e:
        print(f"exception : {str(e)}")
        login_responses.append({"error": f"Exception at iteration {i}: {str(e)}"})

# 2. 제품 주문 요청 (1만 번 반복)
for i in range(1, 1000):
    if i > len(login_responses):
        print(f"No response available for iteration {i}")
        continue

    # Authorization 헤더에 로그인 응답 추가
    login_response_now = login_responses[i - 1]
    token = login_response_now.get("token") if isinstance(login_response_now, dict) else None
    print(f"getTokent : {token}")
    if not token or "error" in login_response_now:
        print(f"Skipping iteration {i} due to missing or invalid token")
        continue

    headers = {
        "Authorization": f"{token}"
    }

    order_body = [
        {
            "productId": 1,
            "productName": "Sample Product",
            "quantity": 1
        }
    ]

    try:
        response = requests.post(order_url, json=order_body, headers=headers)
        if response.status_code == 200:
            print(f"Order successful for iteration {i}: {response.text}")
        else:
            print(f"Order failed at iteration {i}: {response.status_code}")
    except Exception as e:
        print(f"Exception at iteration {i}: {str(e)}")

# 결과 저장
with open("login_responses.json", "w") as login_file:
    json.dump(login_responses, login_file, indent=2)

print("All requests completed.")

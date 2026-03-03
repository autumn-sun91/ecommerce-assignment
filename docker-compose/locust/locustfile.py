from locust import HttpUser, task, between
import random
import os

PRODUCT_ID = 1
TEST_MODE = os.getenv("TEST_MODE", "mixed")
# atomic / lock / mixed


class ShopUser(HttpUser):

    wait_time = between(0.01, 0.2)

    def on_start(self):
        if random.randint(1, 10) <= 3:
            self.user_id = random.randint(1, 10)
        else:
            self.user_id = random.randint(1000, 1000000)

    def build_payload(self):
        return {
            "userId": self.user_id,
            "productId": PRODUCT_ID,
            "quantity": 1
        }

    @task
    def order(self):

        if TEST_MODE == "atomic":
            endpoint = "/orders/atomic"

        elif TEST_MODE == "lock":
            endpoint = "/orders/lock"
        elif TEST_MODE == "preoccupy":
                    endpoint = "/orders/preoccupy"
        else:  # mixed
            endpoint = "/orders/atomic" if random.random() < 0.7 else "/orders/lock"

        self.client.post(endpoint, json=self.build_payload())
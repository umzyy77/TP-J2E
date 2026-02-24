import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 10,
  duration: "30s",
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<500"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080/MasterAnnonce/api";
const USERNAME = __ENV.USERNAME || "admin";
const PASSWORD = __ENV.PASSWORD || "password123";

function login() {
  const payload = JSON.stringify({ username: USERNAME, password: PASSWORD });
  const params = { headers: { "Content-Type": "application/json" } };
  const response = http.post(`${BASE_URL}/login`, payload, params);

  check(response, {
    "login status is 200": (r) => r.status === 200,
    "login has token": (r) => !!r.json("token"),
  });

  return response.json("token");
}

export default function () {
  const token = login();
  const authHeaders = { headers: { Authorization: `Bearer ${token}` } };

  const list = http.get(`${BASE_URL}/annonces?page=0&size=10`);
  check(list, {
    "GET /annonces status is 200": (r) => r.status === 200,
  });

  const profile = http.get(`${BASE_URL}/annonces/99999`, authHeaders);
  check(profile, {
    "GET /annonces/{id} handles not found": (r) => r.status === 404,
  });

  sleep(1);
}

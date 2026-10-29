#!/bin/sh
set -eu

OUT="/work/generated"

mkdir -p "$OUT"
rm -f "$OUT"/*

echo "Generating local CA..."

openssl genrsa -out "$OUT/ca.key" 2048

openssl req \
  -x509 \
  -new \
  -key "$OUT/ca.key" \
  -sha256 \
  -days 365 \
  -out "$OUT/ca.crt" \
  -subj "/CN=FinancialTracker-Local-CA" \
  -addext "basicConstraints=critical,CA:TRUE" \
  -addext "keyUsage=critical,keyCertSign,cRLSign"

echo "Generating OPA server identity..."

openssl genrsa -out "$OUT/opa-server.key" 2048

openssl req \
  -new \
  -key "$OUT/opa-server.key" \
  -out "$OUT/opa-server.csr" \
  -subj "/CN=localhost"

cat > "$OUT/opa-server.ext" <<EOF
basicConstraints=critical,CA:FALSE
keyUsage=critical,digitalSignature,keyEncipherment
extendedKeyUsage=serverAuth
subjectAltName=DNS:localhost,IP:127.0.0.1
EOF

openssl x509 \
  -req \
  -in "$OUT/opa-server.csr" \
  -CA "$OUT/ca.crt" \
  -CAkey "$OUT/ca.key" \
  -CAcreateserial \
  -out "$OUT/opa-server.crt" \
  -days 365 \
  -sha256 \
  -extfile "$OUT/opa-server.ext"

echo "Generating Spring PEP client identity..."

openssl genrsa -out "$OUT/spring-pep.key" 2048

openssl req \
  -new \
  -key "$OUT/spring-pep.key" \
  -out "$OUT/spring-pep.csr" \
  -subj "/CN=spring-pep"

cat > "$OUT/spring-pep.ext" <<EOF
basicConstraints=critical,CA:FALSE
keyUsage=critical,digitalSignature,keyEncipherment
extendedKeyUsage=clientAuth
EOF

openssl x509 \
  -req \
  -in "$OUT/spring-pep.csr" \
  -CA "$OUT/ca.crt" \
  -CAkey "$OUT/ca.key" \
  -CAcreateserial \
  -out "$OUT/spring-pep.crt" \
  -days 365 \
  -sha256 \
  -extfile "$OUT/spring-pep.ext"

echo
echo "Verifying generated certificates..."

openssl verify \
  -CAfile "$OUT/ca.crt" \
  "$OUT/opa-server.crt"

openssl verify \
  -CAfile "$OUT/ca.crt" \
  "$OUT/spring-pep.crt"

echo
echo "mTLS certificate generation completed successfully."
#!/bin/bash

# Check if running as root
if [ "$EUID" -ne 0 ]; then
  echo "Please run the script with root privileges."
  exit 1
fi

# 定义要加载的 LDAP schema
SCHEMAS=(
  core
  cosine
  nis
  inetorgperson
  collective
  corba
  duaconf
  dyngroup
  java
  misc
  openldap
  ppolicy
  pmi
)

for schema in "${SCHEMAS[@]}"; do
  # 加载 schema，忽略错误
  if ldapadd -Y EXTERNAL -H ldapi:/// -f "/etc/openldap/schema/${schema}.ldif"; then
    echo "Loaded ${schema}.ldif successfully."
  else
    echo "Failed to load ${schema}.ldif, continuing to next."
  fi
done

echo "All schemas processed."

exit 0
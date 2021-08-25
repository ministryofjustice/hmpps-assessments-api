{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for web and worker containers
*/}}
{{- define "deployment.envs" }}
env:
  - name: SERVER_PORT
    value: "{{ .Values.image.port }}"

  - name: SPRING_PROFILES_ACTIVE
    value: "{{ .Values.env.SPRING_PROFILES_ACTIVE }}"

  - name: JAVA_OPTS
    value: "{{ .Values.env.JAVA_OPTS }}"

  - name: OAUTH_ENDPOINT_URL
    value: "{{ .Values.env.OAUTH_ENDPOINT_URL }}"

  - name: COURT_CASE_API_BASE_URL
    value: "{{ .Values.env.COURT_CASE_API_BASE_URL }}"

  - name: COURT_CASE_API_ID
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_ID

  - name: COURT_CASE_API_CLIENT_SECRET
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_SECRET

  - name: ASSESSMENT_UPDATE_API_BASE_URL
    value: "{{ .Values.env.ASSESSMENT_UPDATE_API_BASE_URL }}"

  - name: ASSESSMENT_UPDATE_API_ID
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_ID

  - name: ASSESSMENT_UPDATE_API_CLIENT_SECRET
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_SECRET

  - name: ASSESSMENT_API_BASE_URL
    value: "{{ .Values.env.ASSESSMENT_API_BASE_URL }}"

  - name: ASSESSMENT_API_ID
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_ID

  - name: ASSESSMENT_API_CLIENT_SECRET
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_SECRET

  - name: ASSESS_RISKS_AND_NEEDS_API_BASE_URL
    value: "{{ .Values.env.ASSESS_RISKS_AND_NEEDS_API_BASE_URL }}"

  - name: ASSESS_RISKS_AND_NEEDS_API_ID
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_ID

  - name: ASSESS_RISKS_AND_NEEDS_API_CLIENT_SECRET
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_SECRET

  - name: COMMUNITY_API_BASE_URL
    value: "{{ .Values.env.COMMUNITY_API_BASE_URL }}"

  - name: COMMUNITY_API_CLIENT_ID
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_ID

  - name: COMMUNITY_API_CLIENT_SECRET
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: API_CLIENT_SECRET

  - name: DATABASE_USERNAME
    valueFrom:
      secretKeyRef:
        name: hmpps-assessments-rds-instance-output
        key: database_username

  - name: DATABASE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: hmpps-assessments-rds-instance-output
        key: database_password

  - name: DATABASE_NAME
    valueFrom:
      secretKeyRef:
        name: hmpps-assessments-rds-instance-output
        key: database_name

  - name: DATABASE_ENDPOINT
    valueFrom:
      secretKeyRef:
        name: hmpps-assessments-rds-instance-output
        key: rds_instance_endpoint

  - name: APPINSIGHTS_INSTRUMENTATIONKEY
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: APPINSIGHTS_INSTRUMENTATIONKEY

  - name: APPLICATIONINSIGHTS_CONNECTION_STRING
    value: "InstrumentationKey=$(APPINSIGHTS_INSTRUMENTATIONKEY)"

  - name: SPRING_REDIS_HOST
    valueFrom:
      secretKeyRef:
        name: {{ .Values.redis.secret }}
        key: primary_endpoint_address

  - name: SPRING_REDIS_PASSWORD
    valueFrom:
      secretKeyRef:
        name: {{ .Values.redis.secret }}
        key: auth_token

  - name: SPRING_REDIS_SSL
    value: {{ .Values.redis.tlsEnabled | quote }}

  - name: INGRESS_URL
    value: 'https://{{ .Values.ingress.host }}'

{{- end -}}

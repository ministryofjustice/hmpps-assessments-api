{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for web and worker containers
*/}}
{{- define "deployment.envs" }}
env:
  - name: SERVER_PORT
    value: "{{ .Values.image.port }}"

  - name: SPRING_PROFILES_ACTIVE
    value: "logstash,postgres"

  - name: JAVA_OPTS
    value: "{{ .Values.env.JAVA_OPTS }}"

  - name: OAUTH_ENDPOINT_URL
    value: "{{ .Values.env.OAUTH_ENDPOINT_URL }}"

  - name: COURT_CASE_API_BASE_URL
    valie: "{{ .Values.env.COURT_CASE_API_BASE_URL }}"

  - name: COURT_CASE_API_PATH_TEMPLATE
    value: "{{ .Values.env.COURT_CASE_API_PATH_TEMPLATE }}"

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

  - name: ASSESSMENT_UPDATE_API_PATH_TEMPLATE
    value: "{{ .Values.env.ASSESSMENT_UPDATE_PATH_TEMPLATE }}"

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

{{- end -}}

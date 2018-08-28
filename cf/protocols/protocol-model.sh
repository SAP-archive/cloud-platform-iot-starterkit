. ./protocol-config.sh

URL_CAPABILITIES=https://${INSTANCE}/iot/core/api/v1/tenant/${TENANT}/capabilities
URL_SENSOR_TYPES=https://${INSTANCE}/iot/core/api/v1/tenant/${TENANT}/sensorTypes

for DIRECTORY in "$PROTOCOL"/*
do
  if [ -d "$DIRECTORY" ];then

    CAPABILITIES=''

    for FILE in "$DIRECTORY"/"capabilities"/*.json
      do
        if [ -f "$FILE" ];then

          IFS='_' SPLIT=($(basename "$FILE"))
          TYPE=${SPLIT[0]}

          curl -L --silent --header 'Content-Type: application/json' --basic --user "${USER}:${PASSWORD}" "${URL_CAPABILITIES}" --data @"$FILE" --output "$FILE".response

          sed 's/^{"id":"//' "$FILE".response | sed 's/","name".*//'  > "$FILE".id

          ID=$(<"$FILE".id)

          CAPABILITIES=${CAPABILITIES}"{"

          CAPABILITIES=${CAPABILITIES}'"id"'
          CAPABILITIES=${CAPABILITIES}':'
          CAPABILITIES=${CAPABILITIES}'"'
          CAPABILITIES=${CAPABILITIES}$ID
          CAPABILITIES=${CAPABILITIES}'"'

          CAPABILITIES=${CAPABILITIES}","

          CAPABILITIES=${CAPABILITIES}'"type"'
          CAPABILITIES=${CAPABILITIES}':'
          CAPABILITIES=${CAPABILITIES}'"'
          CAPABILITIES=${CAPABILITIES}$TYPE
          CAPABILITIES=${CAPABILITIES}'"'

          CAPABILITIES=${CAPABILITIES}"}"

          CAPABILITIES=${CAPABILITIES}","

          rm -f "$FILE".response
          rm -f "$FILE".id
        fi
      done

      # remove last added comma
      CAPABILITIES=$(sed 's/.$//' <<< ${CAPABILITIES})

      sed 's/PLACEHOLDER/'${CAPABILITIES}'/g' "$DIRECTORY"/sensorTypeTemplate.json > "$DIRECTORY"/sensorType.json

      curl -L --header 'Content-Type: application/json' --basic --user "${USER}:${PASSWORD}" "${URL_SENSOR_TYPES}" --data @"$DIRECTORY"/sensorType.json

      rm -f "$DIRECTORY"/sensorType.json

  fi
done





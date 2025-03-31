FROM node:18

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN chmod +x setup-model.sh

EXPOSE 8081

CMD ["./setup-model.sh"]
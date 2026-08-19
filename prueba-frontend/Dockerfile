# Stage 1: Build stage
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Stage 2: Serve stage
FROM nginx:alpine
COPY --from=build /app/dist/prueba-frontend /usr/share/nginx/html
RUN if [ -d "/usr/share/nginx/html/browser" ]; then cp -r /usr/share/nginx/html/browser/* /usr/share/nginx/html/; fi
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

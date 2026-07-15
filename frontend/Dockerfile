# ---------- Build faza ----------
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
# U Docker-u frontend zove /api (nginx proksira ka backendu)
ENV VITE_API_URL=/api
RUN npm run build

# ---------- Runtime faza (nginx) ----------
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

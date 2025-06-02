FROM node:18-alpine as build

WORKDIR /app

# Sao chép package.json và package-lock.json
COPY package.json package-lock.json ./

# Cài đặt dependencies
RUN npm ci

# Sao chép mã nguồn
COPY . .

# Build ứng dụng
RUN npm run build

# Cài đặt serve - một static file server nhẹ
RUN npm install -g serve

# Expose port 3000
EXPOSE 3000

# Chạy serve với thư mục build
CMD ["serve", "-s", "build", "-l", "3000"]
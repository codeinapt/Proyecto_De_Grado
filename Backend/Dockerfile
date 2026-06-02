# Utilizamos una imagen oficial y ligera de Node.js
FROM node:18-alpine

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos primero los archivos de dependencias
# Esto ayuda a que Docker cachee este paso si las dependencias no cambian
COPY package.json package-lock.json* ./

# Instalamos todas las dependencias
RUN npm install

# Copiamos el resto del código fuente al contenedor
COPY . .

# Exponemos el puerto que usará nuestra aplicación
EXPOSE 3000

# El comando por defecto será correr nuestro script de desarrollo
CMD ["npm", "run", "dev"]

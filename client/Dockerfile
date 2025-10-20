# Dockerfile for Sankshipt Client - Development Server
# NOTE: This Dockerfile is for development purposes only
FROM node:24-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json (if available)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application files
COPY . .

# Expose the port that Vite dev server runs on
EXPOSE 3000

# Define the command to run the development server
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
# Dockerfile for Sankshipt Client - Development Server
# NOTE: This Dockerfile should be run from the project root directory
FROM node:24-alpine

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json (if available) to the client directory
COPY client/package*.json ./client/

# Set working directory to client folder for npm operations
WORKDIR /app/client

# Install dependencies
RUN npm install

# Copy the rest of the client application files
COPY client/ ./

# Expose the port that Vite dev server runs on
EXPOSE 3000

# Define the command to run the development server
# '--host 0.0.0.0' to make the server accessible outside the container
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]



# Resources:
# https://www.docker.com/blog/how-to-dockerize-react-app/

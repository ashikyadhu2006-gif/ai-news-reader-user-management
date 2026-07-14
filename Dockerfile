FROM maven:3.9.6-eclipse-temurin-21

# Install system dependencies for GUI and VNC
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y \
    xvfb \
    x11vnc \
    novnc \
    websockify \
    fluxbox \
    libgtk-3-0 \
    libasound2 \
    libxtst6 \
    libxi6 \
    libxrender1 \
    libgl1-mesa-dri \
    libgl1-mesa-glx \
    libgl1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy pom.xml and download maven dependencies (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the application
COPY src ./src

# Copy the startup script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Compile the application
RUN mvn compile

# Expose port (Railway will set PORT env var, but we expose 8080 as default)
EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]

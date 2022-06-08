import config from dotenv

const urlDomain = config.process.env.BACKENDDOMAIN || "http://localhost:8080";

export {urlDomain};

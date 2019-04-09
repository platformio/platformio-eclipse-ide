package com.insightmachines.pio.installer;

import static com.insightmachines.pio.installer.Core.*;
import static com.insightmachines.pio.installer.Helpers.*;
import static com.insightmachines.pio.installer.Misc.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PlatformIoCore {

	private static final Logger LOGGER = Logger.getLogger(PlatformIoCore.class.getName());

	private static final int UPGRADE_PIOCORE_TIMEOUT = 86400 * 7 * 1000; // 7 days
	private static final String pythonVersion = "2.7.13";
	private static final String pipUrl = "https://files.pythonhosted.org/packages/45/ae/8a0ad77defb7cc903f09e551d88b443304a9bd6e6f124e75c0fbbf6de8f7/pip-18.1.tar.gz";
	private static final String virtualenvUrl = "https://files.pythonhosted.org/packages/4e/8b/75469c270ac544265f0020aa7c4ea925c5284b23e445cf3aa8b99f662690/virtualenv-16.1.0.tar.gz";
	private static final String pioCoreDevelopUrl = "https://github.com/platformio/platformio/archive/develop.zip";

	public static void main(String... args) {
		new PlatformIoCore(args).install();
	}

	private Map<String, Object> params;

	public PlatformIoCore(String... args) {
		params = new HashMap<>();
	}

	private void putParameter(String key, Object value) {
		params.put(key, value);
	}

	@SuppressWarnings("unchecked")
	private String getParameter(String... keys) {
		Map<String, Object> m = params;
		for (String k : keys) {
			Object v = m.get(k);
			if (v instanceof Map) {
				m = (Map<String, Object>) v;
			} else {
				return v.toString();
			}
		}
		return "";
	}

	private Path whereIsPython() {
		// SIMPLIFIED IMPLEMENTATION!
		
		Path pythonExecutable = getPythonExecutable( Boolean.parseBoolean(getParameter("useBuiltinPIOCore")) );
		if (Files.exists(pythonExecutable)) {
			return pythonExecutable;
		}

		if (IS_WINDOWS) {
          return installPythonForWindows();
		}
		return null;
	}

//	  async whereIsPython() {
//		    let status = this.params.pythonPrompt.STATUS_TRY_AGAIN;
//		    do {
//		      const pythonExecutable = await misc.getPythonExecutable(this.params.useBuiltinPIOCore);
//		      if (pythonExecutable) {
//		        return pythonExecutable;
//		      }
//
//		      if (process.platform.startsWith('win')) {
//		        try {
//		          return await this.installPythonForWindows();
//		        } catch (err) {
//		          console.warn(err);
//		        }
//		      }
//
//		      const result = await this.params.pythonPrompt.prompt();
//		      status = result.status;
//		      if (status === this.params.pythonPrompt.STATUS_CUSTOMEXE) {
//		        return result.pythonExecutable;
//		      }
//		    } while (status !== this.params.pythonPrompt.STATUS_ABORT);
//
//		    this.status = BaseStage.STATUS_FAILED;
//		    throw new Error('Can not find Python Interpreter');
//		  }

	private Path installPythonForWindows() {
		// https://www.python.org/ftp/python/2.7.14/python-2.7.14.msi
	    // https://www.python.org/ftp/python/2.7.14/python-2.7.14.amd64.msi
	    String pythonArch = IS_64_BIT ? ".amd64" : "";
	    String msiUrl = "https://www.python.org/ftp/python/" + pythonVersion + "/python-" + pythonVersion + pythonArch + ".msi";
	    Path msiInstaller = download(
	      msiUrl,
	      getCacheDir().resolve(basename(msiUrl))
	    );
	    Path targetDir = getHomeDir().resolve("python27");
	    Path pythonPath = targetDir.resolve("python.exe");
		
	    if (!Files.exists(pythonPath)) {
	      try {
	        installPythonFromWindowsMSI(msiInstaller, targetDir, false);
	      } catch (Exception ex) {
	        LOGGER.warning(ex.getMessage());
	        installPythonFromWindowsMSI(msiInstaller, targetDir, true);
	      }
	    }
	    
	    // append temporary to system environment	    
	    String path = String.join( File.pathSeparator, targetDir.toString(), targetDir.resolve("Scripts").toString(), System.getenv("PATH") );
	    Map<String,String> env = new HashMap<String, String>();
	    env.put("PATH", path);
	    env.put("Path", path);

	    // install virtualenv
	    return runCommand(
	    	"pip",
	        Arrays.asList("install", "virtualenv"),
	        (code, stdout, stderr) -> pythonPath
	    );

	}

//		  async installPythonForWindows() {
//		    // https://www.python.org/ftp/python/2.7.14/python-2.7.14.msi
//		    // https://www.python.org/ftp/python/2.7.14/python-2.7.14.amd64.msi
//		    const pythonArch = process.arch === 'x64' ? '.amd64' : '';
//		    const msiUrl = `https://www.python.org/ftp/python/${PlatformIOCoreStage.pythonVersion}/python-${PlatformIOCoreStage.pythonVersion}${pythonArch}.msi`;
//		    const msiInstaller = await helpers.download(
//		      msiUrl,
//		      path.join(core.getCacheDir(), path.basename(msiUrl))
//		    );
//		    const targetDir = path.join(core.getHomeDir(), 'python27');
//		    const pythonPath = path.join(targetDir, 'python.exe');
//
//		    if (!fs.isFileSync(pythonPath)) {
//		      try {
//		        await this.installPythonFromWindowsMSI(msiInstaller, targetDir);
//		      } catch (err) {
//		        console.warn(err);
//		        await this.installPythonFromWindowsMSI(msiInstaller, targetDir, true);
//		      }
//		    }
//
//		    // append temporary to system environment
//		    process.env.PATH = [targetDir, path.join(targetDir, 'Scripts'), process.env.PATH].join(path.delimiter);
//		    process.env.Path = process.env.PATH;
//
//		    // install virtualenv
//		    return new Promise(resolve => {
//		      misc.runCommand(
//		        'pip',
//		        ['install', 'virtualenv'],
//		        () => resolve(pythonPath)
//		      );
//		    });
//		  }

	private void installPythonFromWindowsMSI(Path msiInstaller, Path targetDir, boolean administrative) {
		Path logFile = getCacheDir().resolve("python27msi.log");
		runCommand(
			"msiexec.exe",
			Arrays.asList(administrative ? "/a" : "/i", '"' + msiInstaller.toString() + '"', "/qn", "/li", '"' + logFile.toString() + '"', "TARGETDIR=\"" + targetDir.toString() + '"'),
			(code, stdout, stderr) -> {
				if (code == 0) {
					return stdout;
				} else {
					try {
						stderr = new String(Files.readAllBytes(logFile));
					} catch (IOException e) {
						LOGGER.warning("Failed to read the log file for Python MSI installer");
					}
					throw new RuntimeException("MSI Python2.7: " + stderr);
				}
			}
		);

		if (!Files.exists(targetDir.resolve("python.exe"))) {
			throw new RuntimeException("Could not install Python 2.7 using MSI");
		}
	}

//		  async installPythonFromWindowsMSI(msiInstaller, targetDir, administrative = false) {
//		    const logFile = path.join(core.getCacheDir(), 'python27msi.log');
//		    await new Promise((resolve, reject) => {
//		      misc.runCommand(
//		        'msiexec.exe',
//		        [administrative ? '/a' : '/i', `"${msiInstaller}"`, '/qn', '/li', `"${logFile}"`, `TARGETDIR="${targetDir}"`],
//		        (code, stdout, stderr) => {
//		          if (code === 0) {
//		            return resolve(stdout);
//		          } else {
//		            if (fs.isFileSync(logFile)) {
//		              stderr = fs.readFileSync(logFile).toString();
//		            }
//		            return reject(new Error(`MSI Python2.7: ${stderr}`));
//		          }
//		        },
//		        {
//		          spawnOptions: {
//		            shell: true
//		          }
//		        }
//		      );
//		    });
//		    if (!fs.isFileSync(path.join(targetDir, 'python.exe'))) {
//		      throw new Error('Could not install Python 2.7 using MSI');
//		    }
//		  }

	private boolean cleanVirtualEnvDir() {
		Path envDir = getEnvDir();
		if (Files.exists(envDir)) {
			return true;
		}
		try {
			Files.delete(envDir);
			return true;
		} catch (IOException ex) {
			LOGGER.warning(ex.getMessage());
			return false;
		}
	}

//		  cleanVirtualEnvDir() {
//		    const envDir = core.getEnvDir();
//		    if (!fs.isDirectorySync(envDir)) {
//		      return true;
//		    }
//		    try {
//		      fs.removeSync(envDir);
//		      return true;
//		    } catch (err) {
//		      console.warn(err);
//		      return false;
//		    }
//		  }

	boolean isCondaInstalled() {
		return runCommand("conda", "--version") == 0;
	}

//		  isCondaInstalled() {
//		    return new Promise(resolve => {
//		      misc.runCommand('conda', ['--version'], code => resolve(code === 0));
//		    });
//		  }

	private void createVirtualenvWithConda() {
		cleanVirtualEnvDir();
		runCommand(
			"conda", 
			Arrays.asList("create", "--yes", "--quiet", "python=2", "pip", "--prefix", getEnvDir().toString()), 
			(code, stdout, stderr) -> {
				if (code == 0) {
					return stdout;
				} else {
					throw new RuntimeException("Conda Virtualenv: " + stderr);
				}
			}
		);
	}

//		  createVirtualenvWithConda() {
//		    this.cleanVirtualEnvDir();
//		    return new Promise((resolve, reject) => {
//		      misc.runCommand(
//		        'conda',
//		        ['create', '--yes', '--quiet', 'python=2', 'pip', '--prefix', core.getEnvDir()],
//		        (code, stdout, stderr) => {
//		          if (code === 0) {
//		            return resolve(stdout);
//		          } else {
//		            return reject(new Error(`Conda Virtualenv: ${stderr}`));
//		          }
//		        }
//		      );
//		    });
//		  }

	private void createVirtualenvWithLocal() {
		cleanVirtualEnvDir();
		Path pythonExecutable = whereIsPython();
		try {
			runCommand(
	          pythonExecutable,
	          Arrays.asList("-m", "virtualenv", "-p", pythonExecutable.toString(), getEnvDir().toString()),
	          (code, stdout, stderr) -> {
	            if (code == 0) {
	              return stdout;
	            } else {
	              throw new RuntimeException("User's Virtualenv: " + stderr );
	            }
	          }
	        );
		} catch ( Exception ex ) {
			cleanVirtualEnvDir();
	        runCommand(
	          "virtualenv",
	          Arrays.asList("-p", pythonExecutable.toString(), getEnvDir().toString()),
	          (code, stdout, stderr) -> {
	            if (code == 0) {
	              return stdout;
	            } else {
	              throw new RuntimeException("User's Virtualenv: " + stderr);
	            }
	          }
	        );		      
		}
	}

//		  async createVirtualenvWithLocal() {
//		    let result = undefined;
//		    this.cleanVirtualEnvDir();
//		    const pythonExecutable = await this.whereIsPython();
//		    try {
//		      result = await new Promise((resolve, reject) => {
//		        misc.runCommand(
//		          pythonExecutable,
//		          ['-m', 'virtualenv', '-p', pythonExecutable, core.getEnvDir()],
//		          (code, stdout, stderr) => {
//		            if (code === 0) {
//		              return resolve(stdout);
//		            } else {
//		              return reject(new Error(`User's Virtualenv: ${stderr}`));
//		            }
//		          }
//		        );
//		      });
//		    } catch (err) {
//		      this.cleanVirtualEnvDir();
//		      result = await new Promise((resolve, reject) => {
//		        misc.runCommand(
//		          'virtualenv',
//		          ['-p', pythonExecutable, core.getEnvDir()],
//		          (code, stdout, stderr) => {
//		            if (code === 0) {
//		              return resolve(stdout);
//		            } else {
//		              return reject(new Error(`User's Virtualenv: ${stderr}`));
//		            }
//		          }
//		        );
//		      });
//		    }
//		    return result;
//		  }

	private void createVirtualenvWithDownload() {
		// TODO: Implement
	}

//		  async createVirtualenvWithDownload() {
//		    this.cleanVirtualEnvDir();
//		    const archivePath = await helpers.download(
//		      PlatformIOCoreStage.virtualenvUrl,
//		      path.join(core.getCacheDir(), 'virtualenv.tar.gz')
//		    );
//		    const tmpDir = tmp.dirSync({
//		      dir: core.getCacheDir(),
//		      unsafeCleanup: true
//		    }).name;
//		    const dstDir = await helpers.extractTarGz(archivePath, tmpDir);
//		    const virtualenvScript = fs.listTreeSync(dstDir).find(
//		      item => path.basename(item) === 'virtualenv.py');
//		    if (!virtualenvScript) {
//		      throw new Error('Can not find virtualenv.py script');
//		    }
//		    const pythonExecutable = await this.whereIsPython();
//		    return new Promise((resolve, reject) => {
//		      misc.runCommand(
//		        pythonExecutable,
//		        [virtualenvScript, core.getEnvDir()],
//		        (code, stdout, stderr) => {
//		          try {
//		            fs.removeSync(tmpDir);
//		          } catch (err) {
//		            console.warn(err);
//		          }
//		          if (code === 0) {
//		            return resolve(stdout);
//		          } else {
//		            let userNotification = `Virtualenv Create: ${stderr}\n${stdout}`;
//		            if (stderr.includes('WindowsError: [Error 5]')) {
//		              userNotification = `If you use Antivirus, it can block PlatformIO Installer. Try to disable it for a while.\n\n${userNotification}`;
//		            }
//		            return reject(new Error(userNotification));
//		          }
//		        }
//		      );
//		    });
//		  }

	private void installVirtualenvPackage() {
		Path pythonExecutable = whereIsPython();
		runCommand(pythonExecutable.toAbsolutePath().toString(), Arrays.asList("-m", "pip", "install", "virtualenv"),
				(code, stdout, stderr) -> {
					if (code == 0) {
						return stdout;
					} else {
						throw new RuntimeException("Install Virtualenv globally: " + stderr);
					}
				});
	}

//		  async installVirtualenvPackage() {
//		    const pythonExecutable = await this.whereIsPython();
//		    return new Promise((resolve, reject) => {
//		      misc.runCommand(
//		        pythonExecutable,
//		        ['-m', 'pip', 'install', 'virtualenv'],
//		        (code, stdout, stderr) => {
//		          if (code === 0) {
//		            return resolve(stdout);
//		          } else {
//		            return reject(new Error(`Install Virtualenv globally: ${stderr}`));
//		          }
//		        }
//		      );
//		    });
//		  }

	private void createVirtualenv() throws Exception {
		if (isCondaInstalled()) {
			createVirtualenvWithConda();
			return;
		}
		try {
			createVirtualenvWithLocal();
		} catch (Exception ex1) {
			LOGGER.warning(ex1.getMessage());
			try {
				createVirtualenvWithDownload();
			} catch (Exception ex2) {
				LOGGER.warning(ex2.getMessage());
				try {
					installVirtualenvPackage();
					createVirtualenvWithLocal();
				} catch (Exception ex3) {
					// misc.reportError(errDl);
					LOGGER.warning(ex3.getMessage());
					throw new RuntimeException(
							"Could not create PIO Core Virtual Environment. Please create it manually -> http://bit.ly/pio-core-virtualenv \n ${errDl.toString()}");
				}
			}
		}
	}
//		  async createVirtualenv() {
//		    if (await this.isCondaInstalled()) {
//		      return await this.createVirtualenvWithConda();
//		    }
//		    try {
//		      await this.createVirtualenvWithLocal();
//		    } catch (err) {
//		      console.warn(err);
//		      try {
//		        await this.createVirtualenvWithDownload();
//		      } catch (errDl) {
//		        console.warn(errDl);
//		        try {
//		          await this.installVirtualenvPackage();
//		          await this.createVirtualenvWithLocal();
//		        } catch (errPkg) {
//		          misc.reportError(errDl);
//		          console.warn(errPkg);
//		          throw new Error(`Could not create PIO Core Virtual Environment. Please create it manually -> http://bit.ly/pio-core-virtualenv \n ${errDl.toString()}`);
//		        }
//		      }
//		    }
//		  }

	private void upgradePIP(String pythonExecutable) {
		// we use manual downloading to resolve SSL issue with old `pip`
		Path pipArchive = download(pipUrl, getCacheDir().resolve(basename(pipUrl)));
		runCommand(pythonExecutable,
				Arrays.asList("-m", "pip", "install", "-U", pipArchive.toAbsolutePath().toString()),
				(code, stdout, stderr) -> {
					if (code == 0) {
						return stdout;
					} else {
						throw new RuntimeException("Upgrade PIP: " + stderr);
					}
				});
	}

//		  async upgradePIP(pythonExecutable) {
//		    // we use manual downloading to resolve SSL issue with old `pip`
//		    const pipArchive = await helpers.download(
//		      PlatformIOCoreStage.pipUrl,
//		      path.join(core.getCacheDir(), path.basename(PlatformIOCoreStage.pipUrl))
//		    );
//		    return new Promise((resolve, reject) => {
//		      misc.runCommand(pythonExecutable, ['-m', 'pip', 'install', '-U', pipArchive], (code, stdout, stderr) => {
//		        return code === 0 ? resolve(stdout) : reject(stderr);
//		      });
//		    });
//		  }

	private boolean installPIOCore() {
		// TODO: Implement
		return false;
	}

//		  async installPIOCore() {
//		    const pythonExecutable = await this.whereIsPython();
//
//		    // Try to upgrade PIP to the latest version with updated openSSL
//		    try {
//		      await this.upgradePIP(pythonExecutable);
//		    } catch (err) {
//		      console.warn(err);
//		      misc.reportError(new Error(`Upgrade PIP: ${err.toString()}`));
//		    }
//
//		    // Install dependencies
//		    const args = ['-m', 'pip', 'install', '-U'];
//		    if (this.params.useDevelopmentPIOCore) {
//		      args.push(PlatformIOCoreStage.pioCoreDevelopUrl);
//		    } else {
//		      args.push('platformio');
//		    }
//		    return new Promise((resolve, reject) => {
//		      misc.runCommand(pythonExecutable, args, (code, stdout, stderr) => {
//		        if (code === 0) {
//		          resolve(stdout);
//		        } else {
//		          if (misc.IS_WINDOWS) {
//		            stderr = `If you have antivirus/firewall/defender software in a system, try to disable it for a while. \n ${stderr}`;
//		          }
//		          return reject(new Error(`PIP Core: ${stderr}`));
//		        }
//		      });
//		    });
//		  }

	private void installPIOHome() {
		runPIOCommand(Arrays.asList("home", "--host", "__do_not_start__"), (code, stdout, stderr) -> {
			if (code != 0) {
				LOGGER.warning(stdout + "\n" + stderr);
			}
			return null;
		});
	}

//		  installPIOHome() {
//		    return new Promise(resolve => {
//		      core.runPIOCommand(
//		        ['home', '--host', '__do_not_start__'],
//		        (code, stdout, stderr) => {
//		          if (code !== 0) {
//		            console.warn(stdout, stderr);
//		          }
//		          return resolve(true);
//		        }
//		      );
//		    });
//		  }

	private void initState() {
		// NOTE: Can be ignored for now
	}
//		  initState() {
//		    let state = this.state;
//		    if (!state || !state.hasOwnProperty('pioCoreChecked') || !state.hasOwnProperty('lastIDEVersion')) {
//		      state = {
//		        pioCoreChecked: 0,
//		        lastIDEVersion: null
//		      };
//		    }
//		    return state;
//		  }

	private void autoUpgradePIOCore(String currentCoreVersion) {
		// NOTE: Can be ignored for now
	}
//		  async autoUpgradePIOCore(currentCoreVersion) {
//		    const newState = this.initState();
//		    const now = new Date().getTime();
//		    if (
//		      (process.env.PLATFORMIO_IDE && newState.lastIDEVersion && newState.lastIDEVersion !== process.env.PLATFORMIO_IDE)
//		      || ((now - PlatformIOCoreStage.UPGRADE_PIOCORE_TIMEOUT) > parseInt(newState.pioCoreChecked))
//		    ) {
//		      newState.pioCoreChecked = now;
//		      // PIO Core
//		      await new Promise(resolve => {
//		        core.runPIOCommand(
//		          ['upgrade', ...(this.params.useDevelopmentPIOCore && !semver.prerelease(currentCoreVersion) ? ['--dev'] : [])],
//		          (code, stdout, stderr) => {
//		            if (code !== 0) {
//		              console.warn(stdout, stderr);
//		            }
//		            resolve(true);
//		          }
//		        );
//		      });
//		    }
//		    newState.lastIDEVersion = process.env.PLATFORMIO_IDE;
//		    this.state = newState;
//		  }

	private void check() {
		// NOTE: Can be ignored for now
	}

//		  async check() {
//		    const coreVersion = helpers.PEPverToSemver(await core.getVersion());
//
//		    if (this.params.useBuiltinPIOCore) {
//		      if (!fs.isDirectorySync(core.getEnvBinDir())) {
//		        throw new Error('Virtual environment is not created');
//		      }
//		      else if (semver.lt(coreVersion, '3.5.0-rc.4')) {
//		        throw new Error('Force new python environment');
//		      }
//		      try {
//		        await this.autoUpgradePIOCore(coreVersion);
//		      } catch (err) {
//		        console.warn(err);
//		      }
//		    }
//
//		    if (semver.lt(coreVersion, this.params.pioCoreMinVersion)) {
//		      this.params.setUseBuiltinPIOCore(true);
//		      this.params.useBuiltinPIOCore = true;
//		      this.params.useDevelopmentPIOCore = this.params.useDevelopmentPIOCore || semver.prerelease(this.params.pioCoreMinVersion);
//		      throw new Error(`Incompatible PIO Core ${coreVersion}`);
//		    }
//
//		    this.status = BaseStage.STATUS_SUCCESSED;
//		    console.info(`Found PIO Core ${coreVersion}`);
//		    return true;
//		  }

	public void install() {
		try {
			createVirtualenv();
			installPIOCore();
			installPIOHome();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//		  async install() {
//		    if (this.status === BaseStage.STATUS_SUCCESSED) {
//		      return true;
//		    }
//		    if (!this.params.useBuiltinPIOCore) {
//		      this.status = BaseStage.STATUS_SUCCESSED;
//		      return true;
//		    }
//		    this.status = BaseStage.STATUS_INSTALLING;
//
//		    try {
//		      await this.createVirtualenv();
//		      await this.installPIOCore();
//		      await this.installPIOHome();
//		    } catch (err) {
//		      misc.reportError(err);
//		      throw err;
//		    }
//
//		    this.status = BaseStage.STATUS_SUCCESSED;
//		    return true;
//		  }

}

package cn.dzangfan.code.exn;

/**
 * Raised when a {@link cn.dzangfan.code.data.EsonValue} is or contains a
 * {@link cn.dzangfan.code.data.EsonID} or a
 * {@link cn.dzangfan.code.data.EsonApplication}
 * 
 * @author Li Dzangfan
 *
 */
@SuppressWarnings("serial")
public class EsonNotFinalValueException extends EsonException {

    @Override
    public String getMessage() {
        return "Value is not been completely evaluated";
    }

}
